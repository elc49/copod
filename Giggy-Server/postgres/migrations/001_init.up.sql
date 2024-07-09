CREATE TABLE IF NOT EXISTS users(
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  phone TEXT NOT NULL UNIQUE,
  username VARCHAR(6) UNIQUE,
  avatar TEXT NOT NULL,
  has_farming_rights BOOLEAN NOT NULL DEFAULT false,
  has_poster_rights BOOLEAN NOT NULL DEFAULT false,
  created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted_at TIMESTAMPTZ
);
CREATE INDEX IF NOT EXISTS phone_idx ON users(phone);

CREATE TABLE IF NOT EXISTS posts(
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  text TEXT NOT NULL,
  image TEXT NOT NULL,
  tags TEXT[] NOT NULL,
  location GEOGRAPHY NOT NULL,
  user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS location_idx ON posts USING GIST(location);

CREATE TABLE IF NOT EXISTS farms(
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  name TEXT NOT NULL,
  thumbnail TEXT NOT NULL,
  user_id UUID NOT NULL REFERENCES users(id) ON  DELETE CASCADE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted_at TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS markets(
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  product TEXT NOT NULL,
  image TEXT NOT NULL,
  volume INTEGER NOT NULL,
  unit TEXT NOT NULL,
  price_per_unit INTEGER NOT NULL,
  location GEOGRAPHY NOT NULL,
  harvest_date TIMESTAMPTZ,
  tag TEXT NOT NULL,
  farm_id UUID NOT NULL REFERENCES farms(id) ON DELETE CASCADE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS orders(
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  volume INTEGER NOT NULL,
  to_be_paid INTEGER NOT NULL,
  customer_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  market_id UUID NOT NULL REFERENCES markets(id) ON DELETE CASCADE,
  farm_id UUID NOT NULL REFERENCES farms(id) ON DELETE CASCADE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS payments(
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  customer TEXT NOT NULL,
  amount INTEGER NOT NULL,
  reason TEXT NOT NULL,
  status TEXT NOT NULL,
  reference_id TEXT,
  user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  order_id UUID REFERENCES orders(id) ON DELETE CASCADE,
  market_id UUID REFERENCES markets(id) ON DELETE CASCADE,
  farm_id UUID REFERENCES farms(id) ON DELETE CASCADE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
