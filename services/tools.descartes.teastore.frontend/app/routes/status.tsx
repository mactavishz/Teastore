
import React from 'react';
import { json, useLoaderData } from '@remix-run/react';
import type { LoaderFunction } from '@remix-run/node';


type Server = {
  host: string;
  port: number;
};

type LoaderData = {
  webuiservers: Server[];
  authenticationservers: Server[];
  persistenceservers: Server[];
  imageservers: Server[];
  recommenderservers: Server[];
  dbfinished: boolean;
  imagefinished: boolean;
  recommenderfinished: boolean;
  noregistry: boolean;
};


const getServersForService = (service: string): Server[] => {
  // todo: make an API call
  return [{ host: 'localhost', port: 8080 }];
};

const checkServiceStatus = (service: string): boolean => {
  // todo: make an API call
  return Math.random() > 0.5; // dummy random status
};

export const loader: LoaderFunction = async () => {
  try {
    const data: LoaderData = {
      webuiservers: getServersForService('WEBUI'),
      authenticationservers: getServersForService('AUTH'),
      persistenceservers: getServersForService('PERSISTENCE'),
      imageservers: getServersForService('IMAGE'),
      recommenderservers: getServersForService('RECOMMENDER'),
      dbfinished: checkServiceStatus('PERSISTENCE'),
      imagefinished: checkServiceStatus('IMAGE'),
      recommenderfinished: checkServiceStatus('RECOMMENDER'),
      noregistry: false
    };

    return json(data);
  } catch (error) {
    console.error('Error fetching service status:', error);
    return json({ ...getEmptyData(), noregistry: true });
  }
};

const getEmptyData = (): LoaderData => ({
  webuiservers: [],
  authenticationservers: [],
  persistenceservers: [],
  imageservers: [],
  recommenderservers: [],
  dbfinished: false,
  imagefinished: false,
  recommenderfinished: false,
  noregistry: true
});

export default function StatusPage() {
  const data = useLoaderData<LoaderData>();

  const getServiceStatus = (
    servers: Server[],
    finished: boolean,
    waitingFor?: 'PERSISTENCE'
  ): Service['status'] => {
    if (servers.length === 0) return 'Offline';
    if (waitingFor === 'PERSISTENCE' && !data.dbfinished) return 'Waiting';
    if (!finished) return 'Generating';
    return 'OK';
  };

  const services: Service[] = [
    { 
      name: 'WebUI', 
      servers: data.webuiservers, 
      status: data.webuiservers.length > 0 ? 'OK' : 'Offline'
    },
    { 
      name: 'Auth', 
      servers: data.authenticationservers, 
      status: data.authenticationservers.length > 0 ? 'OK' : 'Offline'
    },
    { 
      name: 'Persistence', 
      servers: data.persistenceservers, 
      status: getServiceStatus(data.persistenceservers, data.dbfinished)
    },
    { 
      name: 'Recommender', 
      servers: data.recommenderservers, 
      status: getServiceStatus(data.recommenderservers, data.recommenderfinished, 'PERSISTENCE')
    },
    { 
      name: 'Image', 
      servers: data.imageservers, 
      status: getServiceStatus(data.imageservers, data.imagefinished, 'PERSISTENCE')
    },
  ];

  return (
    <div className="container" id="main">
      <div className="row">
        <div className="col-sm-12 col-lg-8 col-lg-offset-2">
          <h2 className="minipage-title">TeaStore Service Status</h2>
          <br/>
          {data.noregistry && <h2>Load Balancer does not work. Is Registry offline?</h2>}
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