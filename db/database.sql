CREATE TABLE weights(
  id              INT PRIMARY KEY,
  measure_date    TEXT NOT NULL,
  weight_kg       REAL NOT NULL,
  fat_percent     REAL,
  water_percent   REAL,
  muscle_percent  REAL
);

CREATE UNIQUE INDEX weights_measure_date_uindex
  ON weights(measure_date);
