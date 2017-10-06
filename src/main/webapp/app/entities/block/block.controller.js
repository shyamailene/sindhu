(function() {
    'use strict';

    angular
        .module('sindhuApp')
        .controller('BlockController', BlockController);

    BlockController.$inject = ['Block', 'BlockSearch'];

    function BlockController(Block, BlockSearch) {

        var vm = this;

        vm.blocks = [];
        vm.clear = clear;
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            Block.query(function(result) {
                vm.blocks = result;
                vm.searchQuery = null;
            });
        }

        function search() {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            BlockSearch.query({query: vm.searchQuery}, function(result) {
                vm.blocks = result;
                vm.currentSearch = vm.searchQuery;
            });
        }

        function clear() {
            vm.searchQuery = null;
            loadAll();
        }    }
})();
