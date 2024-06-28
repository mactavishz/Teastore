import React from 'react';
import { json, useLoaderData } from '@remix-run/react';
import type { LoaderFunction } from '@remix-run/node';
// import { ServiceStatusTable } from './ServiceStatusTable';

type Server = {
  host: string;
  port: number;
};

enum ServiceEnum {
  WEBUI = 'webui',
  AUTH = 'auth',
  PERSISTENCE = 'persistence',
  IMAGE = 'image',
  RECOMMENDER = 'recommender'
}

type LoaderData = {
  services: Service[];
  error: string | null;
};

type HealthResponse = {
  checks: any[];
  status: 'UP' | 'DOWN';
};

const getServersForService = (service: ServiceEnum): Server[] => {
  // Assuming all services are running on their own host with the service name
  return [{ host: service, port: 8080 }];
};

const checkServiceStatus = async (service: ServiceEnum): Promise<boolean> => {
  try {
    const { host, port } = getServersForService(service)[0];
    const response = await fetch(`http://${host}:${port}/health`);
    if (!response.ok) {
      return false;
    }
    const healthData: HealthResponse = await response.json();
    return healthData.status === 'UP';
  } catch (error) {
    console.error(`Error checking status for ${service}:`, error);
    return false;
  }
};

export const loader: LoaderFunction = async () => {
  try {
    const servicesData = await Promise.all(
      Object.values(ServiceEnum).map(async (service) => {
        const servers = getServersForService(service);
        const isHealthy = await checkServiceStatus(service);
        return { service, servers, isHealthy };
      })
    );

    const services: Service[] = servicesData.map(({ service, servers, isHealthy }) => ({
      name: service,
      servers,
      status: getServiceStatus(isHealthy, service)
    }));

    return json<LoaderData>({ services, error: null });
  } catch (error) {
    console.error('Error fetching service status:', error);
    return json<LoaderData>({ services: [], error: 'Failed to fetch service status' });
  }
};


const getServiceStatus = (
  isHealthy: boolean,
  service: ServiceEnum
): 'OK' | 'Offline' => {
  if (service === ServiceEnum.WEBUI) {
    return 'OK';
  }
  
  return isHealthy ? 'OK' : 'Offline';
};

export default function StatusPage() {
  const { services, error } = useLoaderData<LoaderData>();

  return (
    <div className="container" id="main">
      <div className="row">
        <div className="col-sm-12 col-lg-8 col-lg-offset-2">
          <h2 className="minipage-title">TeaStore Service Status</h2>
          <br/>
          {error && <h2>{error}</h2>}
          <p><b>This page does not auto refresh!</b> Refresh manually or start an auto refresh for checking the current status (e.g. to see if database generation has finished).</p>
          <p>
            <b>Note:</b> Database and image generation may take a while.
            Leave the TeaStore in a stable and unused state while the database is generating.
            You may use the TeaStore once the database has finished generating.
            Please wait for the image provider to finish as well before running any performance tests.
          </p>
          <br/>
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