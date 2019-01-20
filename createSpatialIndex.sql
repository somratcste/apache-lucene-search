use ad;

CREATE TABLE IF NOT EXISTS geo (
		itemID INTEGER NOT NULL,
		Location POINT NOT NULL,
		spatial index (Location),
		PRIMARY KEY (itemID)
	) ENGINE=MyISAM;

INSERT INTO geo(itemID, Location)
SELECT item_id, GeomFromWKB(POINT(item_coordinates.latitude, item_coordinates.longitude))
FROM item_coordinates;