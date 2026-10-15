import { setTimeout } from 'node:timers/promises'

const apiUrl = (
  process.env.API_URL ?? 'http://localhost:8081'
).replace(/\/$/, '')

const controller = new AbortController()
const devices = [
  { id: 1, name: 'Heatpump A', baseTemperature: 43 },
  { id: 2, name: 'Heatpump B', baseTemperature: 39 },
]

function stop() {
  controller.abort()
}

process.on('SIGINT', stop)
process.on('SIGTERM', stop)

console.log(`Simulator started. API: ${apiUrl}`)
console.log('Sending demo measurements every 5 seconds.')
console.log('Press Ctrl+C to stop.')

let cycle = 0

while (!controller.signal.aborted) {
  for (const device of devices) {
    if (controller.signal.aborted) break

    const measurement = {
      temperatureCelsius: Number(
        (
          device.baseTemperature +
          Math.sin(cycle / 6 + device.id) * 3 +
          (Math.random() - 0.5)
        ).toFixed(1),
      ),
      pressureBar: Number(
        (
          1.8 +
          Math.sin(cycle / 8 + device.id) * 0.2 +
          (Math.random() - 0.5) * 0.1
        ).toFixed(2),
      ),
    }

    try {
      const response = await fetch(
        `${apiUrl}/api/devices/${device.id}/measurements`,
        {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(measurement),
          signal: AbortSignal.any([
            controller.signal,
            AbortSignal.timeout(5000),
          ]),
        },
      )

      if (!response.ok) {
        throw new Error(`HTTP ${response.status}`)
      }

      await response.json()

      console.log(
        `${new Date().toISOString()} | ${device.name}` +
        ` | ${measurement.temperatureCelsius} C` +
        ` | ${measurement.pressureBar} bar`,
      )
    } catch (error) {
      if (controller.signal.aborted) break

      console.error(
        `${device.name}: ${error.message}. Retrying next cycle.`,
      )
    }
  }

  cycle += 1

  try {
    await setTimeout(5000, undefined, {
      signal: controller.signal,
    })
  } catch {
    break
  }
}

console.log('Simulator stopped.')