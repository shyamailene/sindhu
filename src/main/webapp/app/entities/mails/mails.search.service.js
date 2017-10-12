(function() {
    'use strict';

    angular
        .module('sindhuApp')
        .factory('MailsSearch', MailsSearch);

    MailsSearch.$inject = ['$resource'];

    function MailsSearch($resource) {
        var resourceUrl =  'api/_search/mails/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
