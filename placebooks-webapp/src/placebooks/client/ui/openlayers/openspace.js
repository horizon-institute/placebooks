Proj4js.defs["EPSG:27700"]	= "+proj=tmerc +lat_0=49 +lon_0=-2 +k=0.9996012717 +x_0=400000 +y_0=-100000 +ellps=airy +datum=OSGB36 +units=m +no_defs ";

EPSG4326	= new OpenLayers.Projection( "EPSG:4326" );
EPSG27700	= new OpenLayers.Projection( "EPSG:27700" );

OpenLayers.Layer.UKOrdnanceSurvey = OpenLayers.Class(OpenLayers.Layer.WMS,
{
	initialize:	function(name, options )
				{
					OpenLayers.Layer.WMS.prototype.initialize.apply
						(
							this,
							new Array
								(
									"OpenSpace",
									options.tileURL,
									{ format:'image/png', key:options.key, url:options.hostURL },
									OpenLayers.Util.extend
										(
											options,
											{
												//	UK Ordnance Survey require the 'attribution', or copyright message, in their T&C for using the service.
												attribution:	"&copy; Crown Copyright &amp; Database Right 2008.&nbsp; All rights reserved.<br />" +
																"<a href=\"http://openspace.ordnancesurvey.co.uk/openspace/developeragreement.html#enduserlicense\" target=\"_blank\">End User License Agreement</a>",
												projection:		EPSG27700,
												maxExtent:		new OpenLayers.Bounds( 0, 0, 800000, 1300000 ),
												resolutions:	new Array( 1000, 500, 200, 100, 50, 25, 10, 5, 2.5 ),
												tileSize: 		new OpenLayers.Size(200 , 200)
											}
										)
								)
						);
					},
		
	moveTo:		function( bounds, zoomChanged, dragging )
				{
					var	resolution = this.getResolution();
					this.params = OpenLayers.Util.extend( this.params, {"LAYERS":resolution} );						
					if(resolution == 4)
					{
						this.params = OpenLayers.Util.extend( this.params,{"PRODUCT":"25KR"} );							
					}
					else if(resolution == 2.5)
					{
						this.params = OpenLayers.Util.extend( this.params, {"PRODUCT":"25K"} );							
					}
					else
					{
						delete this.params["PRODUCT"];
					}
					OpenLayers.Layer.WMS.prototype.moveTo.apply(this, new Array(bounds, zoomChanged, dragging) );
				},
		
	CLASS_NAME:		"OpenLayers.Layer.WMS.UKOrdnanceSurvey"
}
);