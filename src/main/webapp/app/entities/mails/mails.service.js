(function() {
    'use strict';
    angular
        .module('sindhuApp')
        .factory('Mails', Mails);

    Mails.$inject = ['$resource', 'DateUtils'];

    function Mails ($resource, DateUtils) {
        var resourceUrl =  'api/mails/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.recdate = DateUtils.convertLocalDateFromServer(data.recdate);
                    }
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.recdate = DateUtils.convertLocalDateToServer(copy.recdate);
                    return angular.toJson(copy);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.recdate = DateUtils.convertLocalDateToServer(copy.recdate);
                    return angular.toJson(copy);
                }
            }
        });
    }
})();
