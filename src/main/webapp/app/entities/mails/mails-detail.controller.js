(function() {
    'use strict';

    angular
        .module('sindhuApp')
        .controller('MailsDetailController', MailsDetailController);

    MailsDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Mails'];

    function MailsDetailController($scope, $rootScope, $stateParams, previousState, entity, Mails) {
        var vm = this;

        vm.mails = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('sindhuApp:mailsUpdate', function(event, result) {
            vm.mails = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
