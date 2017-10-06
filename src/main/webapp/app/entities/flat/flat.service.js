(function() {
    'use strict';
    angular
        .module('sindhuApp')
        .factory('Flat', Flat);

    Flat.$inject = ['$resource'];

    function Flat ($resource) {
        var resourceUrl =  'api/flats/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
