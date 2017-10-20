(function() {
    'use strict';

    angular
        .module('sindhuApp')
        .factory('OwnerdetailsSearch', OwnerdetailsSearch);

    OwnerdetailsSearch.$inject = ['$resource'];

    function OwnerdetailsSearch($resource) {
        var resourceUrl =  'api/_search/ownerdetails/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
