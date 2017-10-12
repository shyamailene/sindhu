(function() {
    'use strict';

    angular
        .module('sindhuApp')
        .controller('MailsDeleteController',MailsDeleteController);

    MailsDeleteController.$inject = ['$uibModalInstance', 'entity', 'Mails'];

    function MailsDeleteController($uibModalInstance, entity, Mails) {
        var vm = this;

        vm.mails = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Mails.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
