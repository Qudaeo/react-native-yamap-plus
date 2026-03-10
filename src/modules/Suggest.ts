import NativeSuggestModule, {type SuggestOptions} from '../spec/NativeSuggestsModule';

const suggest = (query: string, options?: SuggestOptions) => {
  if (options) {
    return NativeSuggestModule.suggestWithOptions(query, options);
  }
  return NativeSuggestModule.suggest(query);
};

export const Suggest = {
  suggest,
  reset: NativeSuggestModule.reset,
};
