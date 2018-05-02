DROP TABLE IF EXISTS weights;

CREATE TABLE weights(
  measure_date    TEXT PRIMARY KEY,
  weight_kg       REAL NOT NULL,
  fat_percent     REAL,
  water_percent   REAL,
  muscle_percent  REAL
);
