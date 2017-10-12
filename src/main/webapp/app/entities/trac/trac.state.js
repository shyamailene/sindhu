(function() {
    'use strict';

    angular
        .module('sindhuApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('trac', {
            parent: 'entity',
            url: '/trac?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'sindhuApp.trac.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/trac/tracs.html',
                    controller: 'TracController',
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
                    $translatePartialLoader.addPart('trac');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('trac-detail', {
            parent: 'trac',
            url: '/trac/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'sindhuApp.trac.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/trac/trac-detail.html',
                    controller: 'TracDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('trac');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Trac', function($stateParams, Trac) {
                    return Trac.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'trac',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('trac-detail.edit', {
            parent: 'trac-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/trac/trac-dialog.html',
                    controller: 'TracDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Trac', function(Trac) {
                            return Trac.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('trac.new', {
            parent: 'trac',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/trac/trac-dialog.html',
                    controller: 'TracDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                subject: null,
                                type: null,
                                description: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('trac', null, { reload: 'trac' });
                }, function() {
                    $state.go('trac');
                });
            }]
        })
        .state('trac.edit', {
            parent: 'trac',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/trac/trac-dialog.html',
                    controller: 'TracDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Trac', function(Trac) {
                            return Trac.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('trac', null, { reload: 'trac' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('trac.delete', {
            parent: 'trac',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/trac/trac-delete-dialog.html',
                    controller: 'TracDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Trac', function(Trac) {
                            return Trac.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('trac', null, { reload: 'trac' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
