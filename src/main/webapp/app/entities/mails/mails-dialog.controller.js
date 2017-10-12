(function() {
    'use strict';

    angular
        .module('sindhuApp')
        .controller('MailsDialogController', MailsDialogController);

    MailsDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Mails'];

    function MailsDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Mails) {
        var vm = this;

        vm.mails = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.mails.id !== null) {
                Mails.update(vm.mails, onSaveSuccess, onSaveError);
            } else {
                Mails.save(vm.mails, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('sindhuApp:mailsUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.recdate = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
