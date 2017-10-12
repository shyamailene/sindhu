(function() {
    'use strict';

    angular
        .module('sindhuApp')
        .controller('TracDeleteController',TracDeleteController);

    TracDeleteController.$inject = ['$uibModalInstance', 'entity', 'Trac'];

    function TracDeleteController($uibModalInstance, entity, Trac) {
        var vm = this;

        vm.trac = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Trac.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
