import { withBackgrounds } from "@storybook/addon-ondevice-backgrounds";

export const decorators = [withBackgrounds];

export const parameters = {
  backgrounds: {
    default: "plain",
    values: [
      { name: "plain", value: "#ffffff" },
      { name: "warm", value: "#f4f4f5" },
    ],
  },
  controls: {
    matchers: {
      color: /(background|color)$/i,
      date: /Date$/,
    },
  },
};
