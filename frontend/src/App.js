import "./App.css";
import { useState, useEffect } from "react";
import SimpleBackdrop from "./components/Backdrop";
import ServiceTable from "./components/ServiceTable";
import ServiceForm from "./components/ServiceForm";
import Header from "./components/Header";

function App() {
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [services, setServices] = useState({});

  useEffect(() => {
    const sse = new EventSource("http://localhost:8080/api/v1/status");
    sse.onmessage = (e) => {
      console.log(e);
      setServices(JSON.parse(e.data));
      setLoading(false);
      setSaving(false);
    };
    sse.onerror = () => sse.close();
    return () => {
      sse.close();
    };
  }, []);

  const addService = (name, url) => {
    fetch(`http://localhost:8080/api/v1/services?name=${name}&url=${url}`, {
      method: "POST",
    });
  };

  const deleteService = (id) => {
    fetch(`http://localhost:8080/api/v1/services/${id}`, {
      method: "DELETE",
    }).catch((error) => console.log(error));
  };

  return loading ? (
    <SimpleBackdrop />
  ) : (
    <div className="App">
      <header className="App-header">
        <Header text={"Is It Up?"} />
        <ServiceForm
          addService={addService}
          saving={saving}
          setSaving={setSaving}
        />
        <ServiceTable services={services} deleteService={deleteService} />
      </header>
    </div>
  );
}

export default App;
