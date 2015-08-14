'use strict';
define([
    'angular',
    'underscore',
    'configuration',
    'bootstrap',
    'angularRoute',
    'angularResource'
], function(angular, _, config) {
    angular.module('ticketMonster.venuesView', ['ngRoute', 'ngResource'])
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
                		});
                	}
                }
            };
        })
        .factory('VenueResource', function($resource){
            var resource = $resource(config.baseUrl + 'rest/venues/:venueId',{venueId:'@id'},{'queryAll':{method:'GET',isArray:true},'query':{method:'GET',isArray:false},'update':{method:'PUT'}});
            return resource;
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