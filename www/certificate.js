
var exec = require('cordova/exec');

module.exports = {
    isCertificate: function(onSuccess, onError) {
        exec(onSuccess, onError, 'Certificate', 'isCertificate', []);
    },
    isPinCertificate: function(onSuccess, onError) {
        exec(onSuccess, onError, 'Certificate', 'isPinCertificate', []);
    }
};

    