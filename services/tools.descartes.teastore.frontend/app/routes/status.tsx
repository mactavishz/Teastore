
import React from 'react';


export default function StatusPage() {
  // dummy data.
  const services = [
    {
      name: 'WebUI',
      servers: [{ host: 'localhost', port: 8080 }],
      status: 'OK' as const,
    },
    {
      name: 'Auth',
      servers: [{ host: 'localhost', port: 8081 }],
      status: 'OK' as const,
    },
    {
      name: 'Persistence',
      servers: [{ host: 'localhost', port: 8082 }],
      status: 'Generating' as const,
    },
    {
      name: 'Recommender',
      servers: [{ host: 'localhost', port: 8083 }],
      status: 'Waiting' as const,
    },
    {
      name: 'Image',
      servers: [{ host: 'localhost', port: 8084 }],
      status: 'Generating' as const,
    },
  ];


  return (
    <div className="container" id="main">
      <div className="row">
        <div className="col-sm-12 col-lg-8 col-lg-offset-2">
          <h2 className="minipage-title">TeaStore Service Status</h2>
          <br/>
          <p><b>This page does not auto refresh!</b> Refresh manually or start an auto refresh for checking the current status (e.g. to see if database generation has finished).</p>
          <p>
            <b>Note:</b> Database and image generation may take a while.
            Leave the TeaStore in a stable and unused state while the database is generating.
            You may use the TeaStore once the database has finished generating.
            Please wait for the image provider to finish as well before running any performance tests.
          </p>
          <br/>
          {/* form and table */}
          <ServiceStatusTable services={services} />
          <button className="btn errorbtn" onClick={() => window.location.href = '/'}>
            Back to Shop
          </button>
        </div>
      </div>
    </div>
  );
}



{/* form and table */}
type Service = {
  name: string;
  servers: { host: string; port: number }[];
  status: 'OK' | 'Offline' | 'Generating' | 'Waiting' | 'Training';
};

type ServiceStatusTableProps = {
  services: Service[];
};

export function ServiceStatusTable({ services }: ServiceStatusTableProps) {
  const getStatusClass = (status: Service['status']) => {
    switch (status) {
      case 'OK':
        return 'success';
      case 'Offline':
        return 'danger';
      case 'Generating':
      case 'Waiting':
      case 'Training':
        return 'warning';
      default:
        return '';
    }
  };

  return (
    <table className="table">
      <thead>
        <tr>
          <th>Service</th>
          <th>#</th>
          <th>Host(s)</th>
          <th>Status</th>
        </tr>
      </thead>
      <tbody>
        {services.map((service) => (
          <tr key={service.name}>
            <td>{service.name}</td>
            <td className={service.servers.length < 1 ? 'danger' : ''}>
              {service.servers.length}
            </td>
            <td>
              {service.servers.map((server, index) => (
                <React.Fragment key={index}>
                  {server.host}:{server.port}<br />
                </React.Fragment>
              ))}
            </td>
            <td className={getStatusClass(service.status)}>
              {service.status}
              {['Generating', 'Waiting', 'Training'].includes(service.status) && (
                <span className="statusloader"></span>
              )}
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}