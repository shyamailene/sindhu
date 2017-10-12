(function() {
    'use strict';

    angular
        .module('sindhuApp')
        .factory('TracSearch', TracSearch);

    TracSearch.$inject = ['$resource'];

    function TracSearch($resource) {
        var resourceUrl =  'api/_search/tracs/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
