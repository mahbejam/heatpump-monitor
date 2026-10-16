import { useEffect, useState } from 'react'

type DeviceAlert = {
  code: string
  message: string
  actualValue: number
  threshold: number
  unit: string
}

type AlertResponse = {
  deviceId: number
  measurementId: number | null
  recordedAt: string | null
  alerts: DeviceAlert[]
}

type Props = {
  deviceId: number
  deviceName: string
}

const alertLabels: Record<string, string> = {
  HIGH_TEMPERATURE: 'Temperatur zu hoch',
  LOW_PRESSURE: 'Druck zu niedrig',
  HIGH_PRESSURE: 'Druck zu hoch',
}

export default function AlertPanel({ deviceId, deviceName }: Props) {
  const [data, setData] = useState<AlertResponse | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [refresh, setRefresh] = useState(0)

  useEffect(() => {
    const controller = new AbortController()
    let fetching = false

    setData(null)
    setLoading(true)
    setError('')

    async function loadAlerts() {
      if (fetching || controller.signal.aborted) return
      fetching = true

      try {
        const response = await fetch(
          `/api/devices/${deviceId}/alerts`,
          { signal: controller.signal },
        )

        if (!response.ok) {
          throw new Error(`HTTP ${response.status}`)
        }

        const result: AlertResponse = await response.json()

        if (!controller.signal.aborted) {
          setData(result)
          setError('')
        }
      } catch {
        if (!controller.signal.aborted) {
          setError('Warnungen konnten nicht geladen werden.')
        }
      } finally {
        fetching = false

        if (!controller.signal.aborted) {
          setLoading(false)
        }
      }
    }

    void loadAlerts()

    const intervalId = window.setInterval(() => {
      void loadAlerts()
    }, 10_000)

    return () => {
      window.clearInterval(intervalId)
      controller.abort()
    }
  }, [deviceId, refresh])

  const current = data?.deviceId === deviceId ? data : null

  return (
    <section
      aria-label={`Warnungen: ${deviceName}`}
      style={{
        marginTop: 24,
        padding: 20,
        border: '1px solid #94a3b8',
        borderRadius: 12,
      }}
    >
      <h2>Warnungen · {deviceName}</h2>

      <p>
        Demo-Regeln: Temperatur &gt; 60 °C, Druck &lt; 1 bar
        oder &gt; 3 bar. Keine technischen Betriebsgrenzen.
      </p>

      <button
        type="button"
        onClick={() => setRefresh((value) => value + 1)}
        disabled={loading}
      >
        Warnungen aktualisieren
      </button>

      {loading && <p>Warnungen werden geladen …</p>}

      {error && <p role="alert">{error}</p>}

      {!loading && !error && current && (
        <>
          {current.recordedAt === null ? (
            <p>
              Noch keine Messwerte vorhanden.
              Eine Auswertung ist nicht möglich.
            </p>
          ) : (
            <>
              <p>
                Bewerteter Messwert:{' '}
                {new Date(current.recordedAt).toLocaleString('de-AT')}
              </p>

              {current.alerts.length === 0 ? (
                <p>
                  Keine Warnungen für den zuletzt empfangenen Messwert.
                </p>
              ) : (
                <ul style={{ paddingLeft: 24 }}>
                  {current.alerts.map((alert) => {
                    const unit = alert.unit === 'C' ? '°C' : alert.unit
                    const comparison =
                      alert.code === 'LOW_PRESSURE' ? '<' : '>'

                    return (
                      <li key={alert.code} style={{ marginBottom: 12 }}>
                        <strong>
                          {alertLabels[alert.code] ?? alert.message}
                        </strong>
                        <br />
                        Messwert:{' '}
                        {alert.actualValue.toLocaleString('de-AT')} {unit}
                        {' · '}
                        Demo-Regel: {comparison}{' '}
                        {alert.threshold.toLocaleString('de-AT')} {unit}
                      </li>
                    )
                  })}
                </ul>
              )}
            </>
          )}
        </>
      )}
    </section>
  )
}