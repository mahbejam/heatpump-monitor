import { useEffect, useState } from 'react'
import {
  CartesianGrid,
  Legend,
  Line,
  LineChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts'

type Measurement = {
  id: number
  deviceId: number
  temperatureCelsius: number
  pressureBar: number
  recordedAt: string
}

type Props = {
  deviceId: number
  deviceName: string
}
export default function MeasurementPanel({ deviceId, deviceName }: Props) {
  const [measurements, setMeasurements] = useState<Measurement[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [refresh, setRefresh] = useState(0)

 useEffect(() => {
  const controller = new AbortController()
  let fetching = false

  setLoading(true)
  setError('')

  async function loadMeasurements() {
    if (fetching || controller.signal.aborted) return
    fetching = true

    try {
      const response = await fetch(
        `/api/devices/${deviceId}/measurements`,
        { signal: controller.signal },
      )

      if (!response.ok) {
        throw new Error(`HTTP ${response.status}`)
      }

      const data: Measurement[] = await response.json()

      if (!controller.signal.aborted) {
        setMeasurements([...data].reverse())
        setError('')
      }
    } catch {
      if (!controller.signal.aborted) {
        setError('Die Messwerte konnten nicht geladen werden.')
      }
    } finally {
      fetching = false

      if (!controller.signal.aborted) {
        setLoading(false)
      }
    }
  }

  void loadMeasurements()

  const intervalId = window.setInterval(() => {
    void loadMeasurements()
  }, 10_000)

  return () => {
    window.clearInterval(intervalId)
    controller.abort()
  }
}, [deviceId, refresh])


  const latest = measurements[measurements.length - 1]

  return (
    <section style={{ marginTop: 32, minWidth: 0 }}>
      <h2>Messwerte · {deviceName}</h2>

      <button
        type="button"
        onClick={() => setRefresh((value) => value + 1)}
        disabled={loading}
      >
        Aktualisieren
      </button>

      {loading && <p>Messwerte werden geladen …</p>}
      {error && <p role="alert">{error}</p>}

      {!loading && !error && !latest && (
        <p>Für dieses Gerät sind noch keine Messwerte vorhanden.</p>
      )}

      {!loading && !error && latest && (
        <>
          <p>
            Temperatur: {latest.temperatureCelsius.toLocaleString('de-AT')} °C
            {' · '}
            Druck: {latest.pressureBar.toLocaleString('de-AT')} bar
          </p>

          <p>
            Zuletzt empfangen:{' '}
            {new Date(latest.recordedAt).toLocaleString('de-AT')}
          </p>

          <div style={{ width: '100%', height: 320 }}>
            <ResponsiveContainer width="100%" height="100%">
              <LineChart
                data={measurements}
                margin={{ top: 16, right: 12, bottom: 16, left: 0 }}
              >
                <CartesianGrid strokeDasharray="3 3" />

                <XAxis
                  dataKey="recordedAt"
                  tickFormatter={(value: string) =>
                    new Date(value).toLocaleTimeString('de-AT')
                  }
                />

                <YAxis
  yAxisId="temperature"
  unit=" °C"
  stroke="#ea580c"
/>

                <YAxis
  yAxisId="pressure"
  orientation="right"
  unit=" bar"
  stroke="#2563eb"
/>

                <Tooltip
                  labelFormatter={(value) =>
                    new Date(String(value)).toLocaleString('de-AT')
                  }
                />

                <Legend />

                <Line
                  yAxisId="temperature"
                  dataKey="temperatureCelsius"
                  name="Temperatur (°C)"
                  stroke="#ea580c"
                  dot={{ r: 4 }}
                  isAnimationActive={false}
                />

                <Line
                  yAxisId="pressure"
                  dataKey="pressureBar"
                  name="Druck (bar)"
                  stroke="#2563eb"
                  strokeDasharray="6 4"
                  dot={{ r: 4 }}
                  isAnimationActive={false}
                />
              </LineChart>
            </ResponsiveContainer>
          </div>
        </>
      )}
    </section>
  )
}