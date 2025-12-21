create table event (
  event_code varchar(16) primary key,
  event_name varchar(64) not null,
  event_type varchar(16) not null,  -- discriminator: TRACK / FIELD
  unit varchar(16) not null,        -- SECONDS / METERS / CENTIMETERS
  a double precision not null,
  b double precision not null,
  c double precision not null
);

-- Placeholder coefficients for now (set real ones later)
insert into event (event_code, event_name, event_type, unit, a, b, c) values
('M100',  '100 m',         'TRACK', 'SECONDS',     0, 0, 0),
('LJ',    'Long jump',     'FIELD', 'CENTIMETERS', 0, 0, 0),
('SP',    'Shot put',      'FIELD', 'METERS',      0, 0, 0),
('HJ',    'High jump',     'FIELD', 'CENTIMETERS', 0, 0, 0),
('M400',  '400 m',         'TRACK', 'SECONDS',     0, 0, 0),
('M110H', '110 m hurdles', 'TRACK', 'SECONDS',     0, 0, 0),
('DT',    'Discus throw',  'FIELD', 'METERS',      0, 0, 0),
('PV',    'Pole vault',    'FIELD', 'CENTIMETERS', 0, 0, 0),
('JT',    'Javelin throw', 'FIELD', 'METERS',      0, 0, 0),
('M1500', '1500 m',        'TRACK', 'SECONDS',     0, 0, 0);
