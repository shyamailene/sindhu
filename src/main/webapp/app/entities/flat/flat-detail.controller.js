(function() {
    'use strict';

    angular
        .module('sindhuApp')
        .controller('FlatDetailController', FlatDetailController);

    FlatDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Flat', 'Block'];

    function FlatDetailController($scope, $rootScope, $stateParams, previousState, entity, Flat, Block) {
        var vm = this;

        vm.flat = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('sindhuApp:flatUpdate', function(event, result) {
            vm.flat = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
