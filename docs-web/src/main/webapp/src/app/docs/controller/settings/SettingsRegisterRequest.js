'use strict';

angular.module('docs').controller('SettingsRegisterRequest', function($scope, $http) {
  $scope.registerRequests = [];

  function parseFormEncodedList(text) {
    if (!text) return [];
    return text.split('\n').map(function(line) {
      var obj = {};
      line.split('&').forEach(function(pair) {
        var idx = pair.indexOf('=');
        if (idx > -1) {
          var key = decodeURIComponent(pair.substring(0, idx));
          var value = decodeURIComponent(pair.substring(idx + 1));
          obj[key] = value;
        }
      });
      return obj;
    });
  }

  function loadRequests() {
    $http.get('/docs-web/api/register-request', { responseType: 'text' }).then(function(res) {
      $scope.registerRequests = parseFormEncodedList(res.data);
    });
  }

  $scope.approve = function(req) {
    $http.post('/docs-web/api/register-request/' + req.id + '/approve').then(function() {
      loadRequests();
    });
  };

  $scope.reject = function(req) {
    $http.post('/docs-web/api/register-request/' + req.id + '/reject').then(function() {
      loadRequests();
    });
  };

  loadRequests();
}); 