(function() {
    'use strict';

    angular
        .module('sindhuApp')
        .controller('OwnerdetailsDialogController', OwnerdetailsDialogController);

    OwnerdetailsDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Ownerdetails', 'Flat'];

    function OwnerdetailsDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Ownerdetails, Flat) {
        var vm = this;

        vm.ownerdetails = entity;
        vm.clear = clear;
        vm.save = save;
        vm.flats = Flat.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.ownerdetails.id !== null) {
                Ownerdetails.update(vm.ownerdetails, onSaveSuccess, onSaveError);
            } else {
                Ownerdetails.save(vm.ownerdetails, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('sindhuApp:ownerdetailsUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
