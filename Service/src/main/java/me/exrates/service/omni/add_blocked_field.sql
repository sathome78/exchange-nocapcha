ALTER TABLE REFILL_REQUEST_ADDRESS ADD blocked tinyint(1) not null default false;

ALTER TABLE
`CURRENCY_PAIR`
  MODIFY COLUMN
  `market` enum(
  'USD',
  'BTC',
  'FIAT',
  'ETH',
  'ICO',
  'USDT'
);