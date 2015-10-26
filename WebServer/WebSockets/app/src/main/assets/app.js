window.onload = function() {
    var ws_url=location.href.replace('http://', 'ws://')+'ss';
    var socket=new WebSocket(ws_url);

    socket.onopen = function(event) {
      // console.log(event.currentTarget.url);
    };

    socket.onerror = function(error) {
      console.log('WebSocket error: ' + error);
    };

    socket.onmessage = function(event) {
      var li=document.createElement("li");

      li.appendChild(document.createTextNode(event.data));
      document.getElementById("transcript").appendChild(li);
    };
}
