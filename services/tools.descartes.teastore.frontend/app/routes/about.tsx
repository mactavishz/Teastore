// export default function AboutPage() {
//   return (
//     <div>About Page</div>
//   )
// }



export default function AboutPage() {
  return (
    <div className="container" id="main">
      <div className="row">
        <div className="col-sm-12 col-md-12">
          <h3>Developer Team</h3>
        </div>
        {/* Developer profiles here */}
        {/* to do */}

        <div className="col-sm-12 col-md-12">
          {/* Descartes Research Group info */}
          <DescartesGroup />
        </div>
      </div>
    </div>
  );
};


interface DescartesGroupProps {
  logoUrl: string;
}

// export default DescartesGroup;



// const DescartesGroup: React.FC<DescartesGroupProps> = ({ logoUrl }) => {
export function DescartesGroup(){
  return (
    <div>
      <h3>We are part of the Descartes Research Group:</h3>
      <a href="http://www.descartes.tools" target="_blank" rel="noopener noreferrer">
        <img
          // src={logoUrl}
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
};
