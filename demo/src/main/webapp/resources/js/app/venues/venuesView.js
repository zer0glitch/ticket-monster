'use strict';
define([
    'angular',
    'underscore',
    'configuration',
    'bootstrap',
    'angularRoute',
    'angularResource',
    'app/api/services'
], function(angular, _, config) {
    angular.module('ticketMonster.venuesView', ['ngRoute', 'ngResource', 'ticketMonster.api'])
        .config(['$routeProvider', function($routeProvider) {
            $routeProvider.when('/venues', {
                templateUrl: 'resources/js/app/venues/venues.html',
                controller: 'VenuesController'
            });
        }])
        .directive('menuPopover', function () {
            return {
                restrict: 'A',
                template: '',
                link: function(scope, el, attrs) {
                	if(!Modernizr.touch) {
                		$(el).popover({
                			trigger: 'hover',
                			container: '#content',
                			content: attrs.content,
                			title: attrs.originalTitle
                        }).data('bs.popover')
                            .tip()
                            .addClass('visible-lg')
                            .addClass('visible-md');
                	}
                }
            };
        })
        .controller('VenuesController', ['$scope','VenueResource', function($scope, VenueResource) {
        	$scope.config = config;
            $scope.venues = VenueResource.queryAll(function(data) {
                $scope.venues = data;
                $scope.cities = _.uniq(
                    _.map($scope.venues, function(venue){
                        return venue.address.city;
                    })
                );
                $('.carousel-inner').find('.item:first').addClass('active');
                $(".carousel").carousel();
            });
        }]);
});