(function() {
    'use strict';

    angular
        .module('sindhuApp')
        .controller('TracDetailController', TracDetailController);

    TracDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Trac'];

    function TracDetailController($scope, $rootScope, $stateParams, previousState, entity, Trac) {
        var vm = this;

        vm.trac = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('sindhuApp:tracUpdate', function(event, result) {
            vm.trac = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
