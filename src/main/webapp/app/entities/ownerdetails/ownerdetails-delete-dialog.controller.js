(function() {
    'use strict';

    angular
        .module('sindhuApp')
        .controller('OwnerdetailsDeleteController',OwnerdetailsDeleteController);

    OwnerdetailsDeleteController.$inject = ['$uibModalInstance', 'entity', 'Ownerdetails'];

    function OwnerdetailsDeleteController($uibModalInstance, entity, Ownerdetails) {
        var vm = this;

        vm.ownerdetails = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Ownerdetails.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
