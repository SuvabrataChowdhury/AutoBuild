type Props = {
  commands: string; // now a string
  onChange: (cmdText: string) => void;
};

export default function StageCommandsEditor({ commands, onChange }: Props) {
  return (
    <textarea
      className="mt-2 w-full border px-3 py-2 rounded-md font-mono"
      rows={6}
      value={commands}
      onChange={(e) => onChange(e.target.value)}
      placeholder="Enter commands here..."
    />
  );
}
