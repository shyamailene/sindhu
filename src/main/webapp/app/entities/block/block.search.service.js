(function() {
    'use strict';

    angular
        .module('sindhuApp')
        .factory('BlockSearch', BlockSearch);

    BlockSearch.$inject = ['$resource'];

    function BlockSearch($resource) {
        var resourceUrl =  'api/_search/blocks/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
