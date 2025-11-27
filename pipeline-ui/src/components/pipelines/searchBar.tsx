import { Input } from "../ui/input";
import { Search } from "lucide-react";

type SearchBarProps = {
  value: string;
  onChange: (value: string) => void;
};

export function SearchBar({ value, onChange }: SearchBarProps) {
  return (
    <div className="relative w-[350px]">
      <Input
        placeholder="Value"
        value={value}
        onChange={(e) => onChange(e.target.value)}
        className="rounded-full pl-5 pr-12 h-12 bg-white shadow-sm"
      />

      <Search className="absolute right-5 top-1/2 -translate-y-1/2 text-gray-500" />
    </div>
  );
}
