/**
 * Module for the Event model
 */
define([ 
    'configuration',
    'backbone'
], function (config) {
    /**
     * The Event model class definition
     * Used for CRUD operations against individual events
     */
    var SecurityContext = Backbone.Model.extend({
        urlRoot: config.baseUrl + 'rest/login' // the URL for performing CRUD operations
    });
    // export the Event class
    return SecurityContext;
});