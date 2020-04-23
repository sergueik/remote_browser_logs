// based on: https://stackoverflow.com/questions/20907180/getting-console-log-output-from-chrome-with-selenium-python-api-bindings

const logger = 'INJECTED SCRIPT'

info = function(message) {
  if (typeof console !== 'undefined' && console !== null) {
    return console.log('INFO: ' +message);
  }
};

warn = function(message) {
  if (typeof console !== 'undefined' && console !== null) {
    return console.warn('WARN: ' + message);
  }
};

error = function(message) {
  if (typeof console !== 'undefined' && console !== null) {
    return console.error('ERROR: ' + message);
  }
};

gettime = function() {
  var date = new Date();
  var hours = date.getHours();
  var minutes = date.getMinutes();
  if (minutes < 10) {
    minutes = '0' + minutes;
  }
  var seconds = date.getSeconds();
  if (seconds < 10) {
    seconds = '0' + seconds;
  }
  var message = logger + ' ' + hours + ':' + minutes + ':' + seconds;
  info(message);
  warn(message);
  error(message);
  try{
    setTimeout('gettime()', 2400);
  } catch (e) {
  }
}

if (window.attachEvent) {
  window.attachEvent('onload', gettime());
} else {
  if (window.onload) {
    var currentOnload = window.onload;
    var newOnload = function(evt) {
      currentOnload(evt);
      gettime(evt);
    };
    window.onload = newOnload;
  } else {
    window.onload = gettime();
  }
}