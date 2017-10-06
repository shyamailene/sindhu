(function() {
    'use strict';

    angular
        .module('sindhuApp')
        .factory('FlatSearch', FlatSearch);

    FlatSearch.$inject = ['$resource'];

    function FlatSearch($resource) {
        var resourceUrl =  'api/_search/flats/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
