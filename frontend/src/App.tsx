import { useEffect, useState } from 'react'
import MeasurementPanel from './MeasurementPanel'

type Device = {
  id: number
  name: string
  location: string
  status: 'ONLINE' | 'OFFLINE' | 'FAULT'
}

const statusLabels = {
  ONLINE: 'Online',
  OFFLINE: 'Offline',
  FAULT: 'Störung',
}

const statusColors = {
  ONLINE: '#15803d',
  OFFLINE: '#64748b',
  FAULT: '#dc2626',
}

export default function App() {
  const [devices, setDevices] = useState<Device[]>([])
  const [selectedId, setSelectedId] = useState<number | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    const controller = new AbortController()

    async function loadDevices() {
      try {
        const response = await fetch('/api/devices', {
          signal: controller.signal,
        })

        if (!response.ok) {
          throw new Error(`HTTP ${response.status}`)
        }

        const data: Device[] = await response.json()
        setDevices(data)
        setSelectedId(data[0]?.id ?? null)
      } catch {
        if (!controller.signal.aborted) {
          setError('Die Geräte konnten nicht geladen werden.')
        }
      } finally {
        if (!controller.signal.aborted) {
          setLoading(false)
        }
      }
    }

    void loadDevices()

    return () => controller.abort()
  }, [])

  return (
    <main
      style={{
        padding: 32,
        fontFamily: 'Arial, sans-serif',
        background: '#f8fafc',
        color: '#0f172a',
        borderRadius: 16,
        width: 'min(1000px, 90vw)',
        boxSizing: 'border-box',
      }}
    >
      <h1>Heatpump Monitor</h1>
      <p>Remote-Monitoring · Persönliches Lernprojekt</p>

      {loading && <p>Geräte werden geladen …</p>}

      {error && (
        <p role="alert" style={{ color: '#dc2626' }}>
          {error}
        </p>
      )}

      {!loading && !error && (
        <>
          <h2>Geräteübersicht</h2>
          <p>{devices.length} Geräte</p>

          {devices.length === 0 ? (
            <p>Noch keine Geräte vorhanden.</p>
          ) : (
            <>
              <div style={{ overflowX: 'auto' }}>
                <table
                  cellPadding={14}
                  style={{
                    width: '100%',
                    borderCollapse: 'collapse',
                    textAlign: 'left',
                  }}
                >
                  <thead>
                    <tr style={{ background: '#e2e8f0' }}>
                      <th scope="col">Gerät</th>
                      <th scope="col">Standort</th>
                      <th scope="col">Status</th>
                      <th scope="col">Auswahl</th>
                    </tr>
                  </thead>

                  <tbody>
                    {devices.map((device) => (
                      <tr
                        key={device.id}
                        style={{
                          borderBottom: '1px solid #cbd5e1',
                          background:
                            selectedId === device.id
                              ? '#dbeafe'
                              : 'transparent',
                        }}
                      >
                        <td>{device.name}</td>
                        <td>{device.location}</td>
                        <td
                          style={{
                            color: statusColors[device.status],
                            fontWeight: 'bold',
                          }}
                        >
                          {statusLabels[device.status]}
                        </td>
                        <td>
                          <button
                            type="button"
                            aria-pressed={selectedId === device.id}
                            onClick={() => setSelectedId(device.id)}
                          >
                            {selectedId === device.id
                              ? 'Ausgewählt'
                              : 'Messwerte anzeigen'}
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>

              {selectedId !== null && (
                <MeasurementPanel
  key={selectedId}
  deviceId={selectedId}
  deviceName={
    devices.find((device) => device.id === selectedId)?.name
    ?? `Gerät ${selectedId}`
  }
/>
              )}
            </>
          )}
        </>
      )}
    </main>
  )
}