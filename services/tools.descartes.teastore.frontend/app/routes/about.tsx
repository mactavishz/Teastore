
export default function AboutPage() {

  const developers = [
    {
      name: "Jóakim v. Kistowski",
      role: "Team leader, Interfaces, Persistence Provider Service",
      imageUrl: "/images/joakimKistowski.png",
      profileUrl: "https://se.informatik.uni-wuerzburg.de/staff/joakim_kistowski/"
    },
    {
      name: "André Bauer",
      role: "Web design",
      imageUrl: "/images/andreBauer.png",
      profileUrl: "https://se.informatik.uni-wuerzburg.de/staff/andre_bauer/"
    },
    {
      name: "Simon Eismann",
      role: "Auth Service, Docker, Kieker",
      imageUrl: "/images/simonEismann.png",
      profileUrl: "https://se.informatik.uni-wuerzburg.de/staff/simon_eismann/"
    },
    {
      name: "Norbert Schmitt",
      role: "Image Provider Service, Docker, Kieker",
      imageUrl: "/images/norbertSchmitt.png",
      profileUrl: "https://se.informatik.uni-wuerzburg.de/staff/norbert_schmitt/"
    },
    {
      name: "Johannes Grohmann",
      role: "Recommender Service",
      imageUrl: "/images/johannesGrohmann.png",
      profileUrl: "https://se.informatik.uni-wuerzburg.de/staff/johannes_grohmann/"
    },
    {
      name: "Samuel Kounev",
      role: "Supervisor",
      imageUrl: "/images/samuelKounev.png",
      profileUrl: "https://se.informatik.uni-wuerzburg.de/staff/samuel_kounev/"
    }
  ];

  const descartesLogoUrl = '/images/descartesLogo.png';

  return (
    <div className="container" id="main">
      <div className="row">
        <div className="col-sm-12 col-md-12">
          <h3>Developer Team</h3>
        </div>

        {/* Developer profiles */}
        {developers.map((dev, index) => (
          <DeveloperProfile
            key={index}
            name={dev.name}
            role={dev.role}
            imageUrl={dev.imageUrl}
            profileUrl={dev.profileUrl}
          />
        ))}

        <div className="col-sm-12 col-md-12">
          {/* Descartes Research Group info */}
          <DescartesGroup logoUrl={descartesLogoUrl} />
        </div>
      </div>
    </div>
  );
};


{/* Descartes Research Group info */}
interface DescartesGroupProps {
  logoUrl: string;
}

export function DescartesGroup({ logoUrl }: DescartesGroupProps) {
  return (
    <div>
      <h3>We are part of the Descartes Research Group:</h3>
      <a href="http://www.descartes.tools" target="_blank" rel="noopener noreferrer">
        <img
          src={logoUrl}
          alt="Descartes Research Group"
          className="img-rounded img-responsive"
        />
      </a>
      <blockquote>
        <p>
          Our research is aimed at developing novel methods, techniques and tools for the
          engineering of dependable and efficient computer-based systems, including both 
          classical software systems and cyber-physical systems, spanning the following 
          research areas:
        </p>
        <ol>
          <li>Design, modeling and analysis of software systems and IT infrastructures</li>
          <li>Autonomic and self-aware computing</li>
          <li>Benchmarking and experimental analysis</li>
        </ol>
        <p>
          We are focusing on system performance, reliability and energy-efficiency, 
          while considering the following application and technology domains:
        </p>
        <ul>
          <li>Cloud computing, virtualization, software-defined data centers</li>
          <li>Cyber-physical systems, Internet-of-Things, Industry 4.0</li>
        </ul>
        <p>
          Our research is inspired by the vision of Self-Aware Computing Systems, which 
          are a new class of systems designed with built-in model learning and reasoning 
          capabilities enabling autonomic and proactive decision making at run-time.
        </p>
      </blockquote>
    </div>
  );
}




{/* Developer profiles */}
interface DeveloperProfileProps {
  name: string;
  role: string;
  imageUrl: string;
  profileUrl: string;
}

export function DeveloperProfile({ name, role, imageUrl, profileUrl }: DeveloperProfileProps) {
  return (
    <div className="col-sm-4 col-md-4">
      <img src={imageUrl} alt={name} className="img-rounded img-responsive" />
      <blockquote>
        <p>
          <a className="name" target="_blank" rel="noopener noreferrer" href={profileUrl}>
            {name}
          </a>
        </p>
        <small>
          <cite title="Source Title">{role}</cite>
        </small>
      </blockquote>
    </div>
  );
}