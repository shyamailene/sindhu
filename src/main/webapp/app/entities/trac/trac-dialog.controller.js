(function() {
    'use strict';

    angular
        .module('sindhuApp')
        .controller('TracDialogController', TracDialogController);

    TracDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Trac'];

    function TracDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Trac) {
        var vm = this;

        vm.trac = entity;
        vm.clear = clear;
        vm.save = save;
        vm.issues = [
            {issue : "Electrical"},
            {issue : "Carpenter"},
            {issue : "CommonArea"},
            {issue : "Plumbing"},
            {issue : "Security"},
            {issue : "HouseKeeping"},
            {issue : "Other"}
        ];


        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.trac.id !== null) {
                Trac.update(vm.trac, onSaveSuccess, onSaveError);
            } else {
                Trac.save(vm.trac, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('sindhuApp:tracUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
