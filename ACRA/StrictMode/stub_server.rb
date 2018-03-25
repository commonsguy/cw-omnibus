require 'fileutils'
require 'sinatra'
require 'json'

LOG_ROOT='/tmp/ACRAfier'

put '/reports/:id' do
  acra=JSON.parse(request.body.read)
  FileUtils.mkdir_p(LOG_ROOT) if !File.exist?(LOG_ROOT)

  f=File.join(LOG_ROOT, params[:id]+'.json') 
  File.open(f, 'w') {|io| io.write(JSON.pretty_generate(acra))}
end
