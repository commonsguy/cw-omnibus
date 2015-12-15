require 'fileutils'
require 'time'
require 'sinatra'
require 'json'

BACKUP_ROOT='/tmp/backups'

get '/' do
  'Hello world!'
end

get '/api/backups' do
  result=[]

  if File.exist?(BACKUP_ROOT)
    Dir.foreach(BACKUP_ROOT) do |item|
      next if item == '.' or item == '..'

      subdir=File.join(BACKUP_ROOT, item)

      if File.directory?(subdir)
        f=File.join(subdir, "metadata.json")

        if File.exist?(f)
          metadata=JSON.load(open(f))
          metadata['dataset']="/api/backups/#{item}/dataset"

          result << metadata
        end
      end
    end
  end

  result.sort_by!{|metadata| metadata['timestamp']}
  result.reverse!

  JSON.pretty_generate(result)
end

post '/api/backups' do
  id=SecureRandom.uuid
  dir=File.join(BACKUP_ROOT, id)
  FileUtils.mkdir_p(dir)
  f=File.join(dir, "metadata.json")
  metadata={'timestamp'=>Time.new.xmlschema}
  File.open(f, 'w') {|io| io.write(JSON.generate(metadata))}

  redirect to('/api/backups/'+id), 201
end

put '/api/backups/:id/dataset' do
  dir=File.join(BACKUP_ROOT, params[:id])

  if File.exist?(dir)
    f=File.join(dir, "backup.zip")
    File.open(f, 'w') {|io| io.write(request.body.read)}

    redirect to("/api/backups/#{params[:id]}/dataset"), 201
  else
    status 404
  end
end

get '/api/backups/:id/dataset' do
  dir=File.join(BACKUP_ROOT, params[:id])
  f=File.join(dir, "backup.zip")

  if File.exist?(f)
    send_file f
  else
    status 404
  end
end