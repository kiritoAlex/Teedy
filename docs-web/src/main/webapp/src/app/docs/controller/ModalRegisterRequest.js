'use strict';

/**
 * Modal register request controller.
 */
angular.module('docs').controller('ModalRegisterRequest', function ($scope, $uibModalInstance, $http) {
    $scope.register = {};
    $scope.submit = function(register) {
        var data = $.param(register);
        $http.post('/docs-web/api/register-request', data, {
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
        }).then(function() {
            $uibModalInstance.close(register);
        });
    };
    $scope.cancel = function() {
      $uibModalInstance.dismiss('cancel');
    };
});