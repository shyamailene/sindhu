(function() {
    'use strict';
    angular
        .module('sindhuApp')
        .factory('Ownerdetails', Ownerdetails);

    Ownerdetails.$inject = ['$resource'];

    function Ownerdetails ($resource) {
        var resourceUrl =  'api/ownerdetails/:id';

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
