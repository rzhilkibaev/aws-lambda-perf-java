create table output(
    ts_utc text not null,
    invocation integer not null,
    name text not null,
    memory_size_mb integer not null,
    invoker text not null,
    state text not null,
    duration_ms integer not null
);

.mode csv
.import output.csv output

-- delete csv headers
delete from output where rowid = 1;

.mode column

-- lambda cold
.headers off
select 'lambda + cold';

.headers on
select
    o.invocation,
    avg(case when o.memory_size_mb = 256  then o.duration_ms end) '256',
    avg(case when o.memory_size_mb = 512  then o.duration_ms end) '512',
    avg(case when o.memory_size_mb = 1024 then o.duration_ms end) '1024',
    avg(case when o.memory_size_mb = 1536 then o.duration_ms end) '1536',
    1 dummy

from output o
where
    o.name = 'dummy' and
    o.invoker = 'lambda' and
    o.state = 'cold'
group by o.invocation;

-- api_gw cold
.headers off
select '';
select 'api_gw + cold';

.headers on
select
    o.invocation,
    avg(case when o.memory_size_mb = 256  then o.duration_ms end) '256',
    avg(case when o.memory_size_mb = 512  then o.duration_ms end) '512',
    avg(case when o.memory_size_mb = 1024 then o.duration_ms end) '1024',
    avg(case when o.memory_size_mb = 1536 then o.duration_ms end) '1536',
    1 dummy

from output o
where
    o.name = 'dummy' and
    o.invoker = 'api_gw' and
    o.state = 'cold'
group by o.invocation;

-- lambda warm
.headers off
select '';
select 'api_gw + warm';

.headers on
select
    o.invocation,
    avg(case when o.memory_size_mb = 256  then o.duration_ms end) '256',
    avg(case when o.memory_size_mb = 512  then o.duration_ms end) '512',
    avg(case when o.memory_size_mb = 1024 then o.duration_ms end) '1024',
    avg(case when o.memory_size_mb = 1536 then o.duration_ms end) '1536',
    1 dummy

from output o
where
    o.name = 'dummy' and
    o.invoker = 'lambda' and
    o.state = 'warm'
group by o.invocation;

-- api_gw warm
.headers off
select '';
select 'api_gw + warm';

.headers on
select
    o.invocation,
    avg(case when o.memory_size_mb = 256  then o.duration_ms end) '256',
    avg(case when o.memory_size_mb = 512  then o.duration_ms end) '512',
    avg(case when o.memory_size_mb = 1024 then o.duration_ms end) '1024',
    avg(case when o.memory_size_mb = 1536 then o.duration_ms end) '1536',
    1 dummy

from output o
where
    o.name = 'dummy' and
    o.invoker = 'api_gw' and
    o.state = 'warm'
group by o.invocation;
