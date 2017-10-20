(function() {
    'use strict';

    angular
        .module('sindhuApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('ownerdetails', {
            parent: 'entity',
            url: '/ownerdetails?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'sindhuApp.ownerdetails.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/ownerdetails/ownerdetails.html',
                    controller: 'OwnerdetailsController',
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
                    $translatePartialLoader.addPart('ownerdetails');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('ownerdetails-detail', {
            parent: 'ownerdetails',
            url: '/ownerdetails/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'sindhuApp.ownerdetails.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/ownerdetails/ownerdetails-detail.html',
                    controller: 'OwnerdetailsDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('ownerdetails');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Ownerdetails', function($stateParams, Ownerdetails) {
                    return Ownerdetails.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'ownerdetails',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('ownerdetails-detail.edit', {
            parent: 'ownerdetails-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/ownerdetails/ownerdetails-dialog.html',
                    controller: 'OwnerdetailsDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Ownerdetails', function(Ownerdetails) {
                            return Ownerdetails.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('ownerdetails.new', {
            parent: 'ownerdetails',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/ownerdetails/ownerdetails-dialog.html',
                    controller: 'OwnerdetailsDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                mobile: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('ownerdetails', null, { reload: 'ownerdetails' });
                }, function() {
                    $state.go('ownerdetails');
                });
            }]
        })
        .state('ownerdetails.edit', {
            parent: 'ownerdetails',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/ownerdetails/ownerdetails-dialog.html',
                    controller: 'OwnerdetailsDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Ownerdetails', function(Ownerdetails) {
                            return Ownerdetails.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('ownerdetails', null, { reload: 'ownerdetails' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('ownerdetails.delete', {
            parent: 'ownerdetails',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/ownerdetails/ownerdetails-delete-dialog.html',
                    controller: 'OwnerdetailsDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Ownerdetails', function(Ownerdetails) {
                            return Ownerdetails.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('ownerdetails', null, { reload: 'ownerdetails' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
