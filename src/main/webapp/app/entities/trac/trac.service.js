(function() {
    'use strict';
    angular
        .module('sindhuApp')
        .factory('Trac', Trac);

    Trac.$inject = ['$resource'];

    function Trac ($resource) {
        var resourceUrl =  'api/tracs/:id';

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
