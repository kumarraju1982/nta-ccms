import { useEffect, useState } from "react";
import { fetchCategoryMasters } from "../api/master-api";

export default function AdminMastersPage() {
  const [categories, setCategories] = useState([]);

  useEffect(() => {
    fetchCategoryMasters().then(setCategories).catch(() => setCategories([]));
  }, []);

  return (
    <section className="panel">
      <h3>Admin Master Data</h3>
      <ul>
        {categories.map((cat) => (
          <li key={cat.code}>{cat.name} ({cat.code})</li>
        ))}
      </ul>
    </section>
  );
}
