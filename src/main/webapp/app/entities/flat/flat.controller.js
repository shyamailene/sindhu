(function() {
    'use strict';

    angular
        .module('sindhuApp')
        .controller('FlatController', FlatController);

    FlatController.$inject = ['Flat', 'FlatSearch'];

    function FlatController(Flat, FlatSearch) {

        var vm = this;

        vm.flats = [];
        vm.clear = clear;
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            Flat.query(function(result) {
                vm.flats = result;
                vm.searchQuery = null;
            });
        }

        function search() {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            FlatSearch.query({query: vm.searchQuery}, function(result) {
                vm.flats = result;
                vm.currentSearch = vm.searchQuery;
            });
        }

        function clear() {
            vm.searchQuery = null;
            loadAll();
        }    }
})();
