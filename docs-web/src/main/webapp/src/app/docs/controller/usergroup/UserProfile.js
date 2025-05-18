'use strict';

/**
 * User profile controller.
 */
angular.module('docs').controller('UserProfile', function($stateParams, Restangular, $scope, $state) {
  // Load user
  Restangular.one('user', $stateParams.username).get().then(function(data) {
    $scope.user = data;
  });

  $scope.startChat = function() {
    $state.go('chat', { username: $stateParams.username });
  };
});