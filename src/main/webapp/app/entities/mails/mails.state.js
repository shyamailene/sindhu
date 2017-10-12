(function() {
    'use strict';

    angular
        .module('sindhuApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('mails', {
            parent: 'entity',
            url: '/mails?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'sindhuApp.mails.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/mails/mails.html',
                    controller: 'MailsController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('mails');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('mails-detail', {
            parent: 'mails',
            url: '/mails/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'sindhuApp.mails.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/mails/mails-detail.html',
                    controller: 'MailsDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('mails');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Mails', function($stateParams, Mails) {
                    return Mails.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'mails',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('mails-detail.edit', {
            parent: 'mails-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/mails/mails-dialog.html',
                    controller: 'MailsDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Mails', function(Mails) {
                            return Mails.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('mails.new', {
            parent: 'mails',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/mails/mails-dialog.html',
                    controller: 'MailsDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                from: null,
                                subject: null,
                                mail: null,
                                recdate: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('mails', null, { reload: 'mails' });
                }, function() {
                    $state.go('mails');
                });
            }]
        })
        .state('mails.edit', {
            parent: 'mails',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/mails/mails-dialog.html',
                    controller: 'MailsDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Mails', function(Mails) {
                            return Mails.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('mails', null, { reload: 'mails' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('mails.delete', {
            parent: 'mails',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/mails/mails-delete-dialog.html',
                    controller: 'MailsDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Mails', function(Mails) {
                            return Mails.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('mails', null, { reload: 'mails' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
