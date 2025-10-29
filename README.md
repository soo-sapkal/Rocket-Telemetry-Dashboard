# 🚀 Rocket Telemetry Dashboard  

An interactive **Java-based Rocket Telemetry Visualization System** that simulates and analyzes rocket flight data using advanced Object-Oriented Programming concepts.  
The application visualizes a rocket’s flight path, displays telemetry data, classifies flight stages, overlays weather conditions, and generates detailed mission reports.

---

## 📋 Features  

### 🛰️ Core Functionalities  
- **CSV Data Import:** Upload real or simulated telemetry CSV files.  
- **Interactive Table View:** View, sort, and analyze telemetry data directly in the UI.  
- **Stage Classification:** Automatically detects and labels stages such as **Launch**, **Ascent**, **Apogee**, **Descent**, and **Landing**.  
- **Animated Rocket Path:** Displays a parabolic flight trajectory with smooth animation and stage markers.  
- **Clickable Stages:** Jump the rocket to specific stages interactively.  
- **Weather Overlay:** Integrates weather data (wind, temperature, pressure) for the launch location and time.  
- **Simulation Playback Controls:** Play, pause, rewind, and fast-forward the rocket’s animation.  
- **Anomaly Alerts:** Highlights unexpected behaviors like premature parachute deployment.  
- **Comparative Views:** Compare multiple flights or simulation scenarios side-by-side.  
- **PDF Report Generation:** Creates a detailed post-flight analysis report with telemetry statistics, stage timestamps, and environmental challenges.  

---

## 🧠 Object-Oriented Design Principles  

This project demonstrates full application of **OOP concepts** in Java:  

| Concept | Implementation Example |
|----------|------------------------|
| **Encapsulation** | Private class fields with getters/setters in `TelemetryData.java` |
| **Inheritance** | `UIComponent` as an abstract base class extended by UI panels |
| **Abstraction** | Abstract service layer (`DataService<T>`) for data operations |
| **Polymorphism** | Generic stage classification for different telemetry data sources |
| **Interfaces** | `Clickable` interface for interactive stage markers |

---
🧾 Usage Workflow
Step 1: Upload a telemetry CSV file and input the launch date, time, and location.
Step 2: View the telemetry data table.
Step 3: The program automatically classifies stages.
Step 4: Observe the animated rocket flight on a parabolic path.
Step 5: Click stage markers or control playback.
Step 6: Overlay weather conditions.
Step 7: Export a comprehensive PDF mission report.
